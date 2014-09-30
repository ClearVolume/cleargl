package cleargl;

import javax.media.opengl.GLException;

public interface GLCloseable extends AutoCloseable
{
	@Override
	public void close() throws GLException;
}
