/*-
 * #%L
 * ClearGL facade API on top of JOGL.
 * %%
 * Copyright (C) 2014 - 2025 ClearVolume developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package cleargl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLException;

public class GLShader implements GLInterface, GLCloseable {
	private final GL mGL;
	private int mShaderId;
	private final GLShaderType mShaderType;
	private final String mShaderSource;
	private final String mShaderSourcePath;
	private Path mShaderBasePath;
	private final Class<?> mShaderSourceRootClass;
	private HashMap<String, String> mParameters;

	static final HashMap<GLShaderType, Integer> glShaderTypeMapping;

	static {
		glShaderTypeMapping = new HashMap<>();
		glShaderTypeMapping.put(GLShaderType.VertexShader,
				GL2ES2.GL_VERTEX_SHADER);
		glShaderTypeMapping.put(GLShaderType.GeometryShader,
				GL3.GL_GEOMETRY_SHADER);
		glShaderTypeMapping.put(GLShaderType.TesselationControlShader,
				GL3.GL_TESS_CONTROL_SHADER);
		glShaderTypeMapping.put(GLShaderType.TesselationEvaluationShader,
				GL3.GL_TESS_EVALUATION_SHADER);
		glShaderTypeMapping.put(GLShaderType.FragmentShader,
				GL2ES2.GL_FRAGMENT_SHADER);
	}

	private Path getPath(URL p) {
		try {
			return Paths.get(p.toURI());
		} catch (URISyntaxException | FileSystemNotFoundException e) {
			try {
				final URI uri = p.toURI();
				Map<String, String> env = new HashMap<>();
				FileSystem fs = FileSystems.newFileSystem(uri, Collections.EMPTY_MAP);

				return Paths.get(p.toURI());
			} catch (URISyntaxException | FileSystemAlreadyExistsException | IOException ee) {

			}

		}

		return null;
	}

	public GLShader(final GL pGL,
			final Class<?> pRootClass,
			final String pResourceName,
			final GLShaderType pShaderType) throws IOException {
		super();
		mGL = pGL;
		final InputStream lResourceAsStream = pRootClass.getResourceAsStream(pResourceName);
		mShaderSource = new Scanner(lResourceAsStream, "UTF-8").useDelimiter("\\A").next();
		mShaderType = pShaderType;
		mShaderSourcePath = pResourceName;
		mShaderSourceRootClass = pRootClass;
		mParameters = new HashMap<>();
		Path p = getPath(pRootClass.getResource(pResourceName));
		mShaderBasePath = p.getParent();

		// preprocess shader
		final String shaderSourceProcessed = preprocessShader(mShaderSource);

		mShaderId = pGL.getGL3().glCreateShader(glShaderTypeMapping.get(pShaderType));
		mGL.getGL3().glShaderSource(mShaderId, 1, new String[]{shaderSourceProcessed}, null);
		mGL.getGL3().glCompileShader(mShaderId);

	}

	public GLShader(final GL pGL,
			final Class<?> pRootClass,
			final String pResourceName,
			final GLShaderType pShaderType,
			final HashMap<String, String> params) throws IOException {
		super();
		mGL = pGL;
		final InputStream lResourceAsStream = pRootClass.getResourceAsStream(pResourceName);
		mShaderSource = new Scanner(lResourceAsStream, "UTF-8").useDelimiter("\\A").next();
		mShaderType = pShaderType;
		mShaderSourcePath = pResourceName;
		mShaderSourceRootClass = pRootClass;
		mParameters = params;

		Path p = getPath(pRootClass.getResource(pResourceName));
		mShaderBasePath = p.getParent();

		// preprocess shader
		final String shaderSourceProcessed = preprocessShader(mShaderSource);

		mShaderId = pGL.getGL3().glCreateShader(glShaderTypeMapping.get(pShaderType));
		mGL.getGL3().glShaderSource(mShaderId, 1, new String[]{shaderSourceProcessed}, null);
		mGL.getGL3().glCompileShader(mShaderId);

	}

	public GLShader(final GL pGL,
			final String pShaderSourceAsString,
			final GLShaderType pShaderType) throws IOException {
		super();
		mGL = pGL;
		mShaderSource = pShaderSourceAsString;
		mShaderType = pShaderType;
		mShaderSourceRootClass = null;
		mShaderSourcePath = null;
		mParameters = new HashMap<>();
		mShaderBasePath = null;

		// preprocess shader
		final String shaderSourceProcessed = preprocessShader(mShaderSource);

		mShaderId = pGL.getGL3().glCreateShader(glShaderTypeMapping.get(pShaderType));
		mGL.getGL3().glShaderSource(mShaderId, 1, new String[]{shaderSourceProcessed}, null);
		mGL.getGL3().glCompileShader(mShaderId);

	}

	@Override
	public void close() throws GLException {
		mGL.getGL3().glDeleteShader(mShaderId);
	}

	public void setShaderBasePath(final Path path) {
		mShaderBasePath = path;
	}

	public void recompile(final GL pGL) {
		close();

		// preprocess shader
		final String shaderSourceProcessed = preprocessShader(mShaderSource);

		mShaderId = pGL.getGL3().glCreateShader(glShaderTypeMapping.get(mShaderType));
		mGL.getGL3().glShaderSource(mShaderId, 1, new String[]{shaderSourceProcessed}, null);
		mGL.getGL3().glCompileShader(mShaderId);
	}

	public void setParameters(final HashMap<String, String> params) {
		mParameters = params;
	}

	public String preprocessShader(final String source) {
		String effectiveSource = source;
		int startPos = 0;
		int endPos = 0;

		// replace variables
		while ((startPos = effectiveSource.indexOf("%var(")) != -1) {
			endPos = effectiveSource.indexOf(")", startPos);
			final String varName = effectiveSource.substring(startPos + "%var(".length(), endPos);
			if (!mParameters.containsKey(varName)) {
				System.err.println("Warning: Variable '" + varName + "' does not exist in shader parameters!");
			}
			final String varContents = mParameters.getOrDefault(varName, "");

			effectiveSource = effectiveSource.substring(0, startPos) + varContents
					+ effectiveSource.substring(endPos + ")".length());
		}

		// find includes
		startPos = 0;
		endPos = 0;
		while ((startPos = effectiveSource.indexOf("%include <")) != -1) {
			endPos = effectiveSource.indexOf(">", startPos);
			final String includeFileName = effectiveSource.substring(startPos + "%include <".length(), endPos);
			String includeSource = "";

			try {
				includeSource = Files.lines(mShaderBasePath.resolve(includeFileName))
						.parallel()
						.filter(line -> !line.startsWith("//"))
						.map(String::trim)
						.collect(Collectors.joining());
			} catch (final IOException e) {
				e.printStackTrace();
			}

			effectiveSource = effectiveSource.substring(0, startPos) + "\n// included from " + includeFileName + "\n"
					+ includeSource + "\n// end include\n" + effectiveSource.substring(endPos + ">".length());
		}

		return effectiveSource;
	}

	public String getShaderInfoLog() {
		final int logLen = getShaderParameter(GL2ES2.GL_INFO_LOG_LENGTH);
		if (logLen <= 0)
			return "";

		final int[] lLength = new int[1];
		final byte[] lBytes = new byte[logLen + 1];
		mGL.getGL3().glGetShaderInfoLog(mShaderId,
				logLen,
				lLength,
				0,
				lBytes,
				0);
		final String logMessage = toString() + ":\n" + new String(lBytes);
		return logMessage;
	}

	public int getShaderParameter(final int pParamName) {
		final int lParameter[] = new int[1];
		mGL.getGL3().glGetShaderiv(mShaderId, pParamName, lParameter, 0);
		return lParameter[0];
	}

	public String getSourcePath() {
		return mShaderSourcePath;
	}

	public Class<?> getShaderSourceRootClass() {
		return mShaderSourceRootClass;
	}

	@Override
	public int getId() {
		return mShaderId;
	}

	@Override
	public GL getGL() {
		return mGL;
	}

	@Override
	public String toString() {
		return "GLShader ["
				+ "mShaderId="
				+ mShaderId
				+ ", mShaderType="
				+ mShaderType
				+ "]";
	}

	public String toStringDebug() {
		return "GLShader [mGL=" + mGL
				+ ", mShaderId="
				+ mShaderId
				+ ", mShaderType="
				+ mShaderType
				+ ", mShaderSource="
				+ mShaderSource
				+ "]";
	}
}
