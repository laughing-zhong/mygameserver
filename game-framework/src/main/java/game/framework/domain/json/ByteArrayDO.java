package game.framework.domain.json;

/**
 * Domain object that contains a single byte array.
 *
 * @author dadler
 */

public class ByteArrayDO extends CasJsonDO {
	private byte[] byteArray = new byte[0];

	public byte[] getByteArray() {
		return byteArray;
	}

	public void setByteArray( byte[] byteArray ) {
		this.byteArray = byteArray;
	}
}