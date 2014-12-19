package transforms;

public class Mat3Identity extends Mat3 {

	/**
	 * Vytvari jednotkovou matici 3x3
	 */
	public Mat3Identity() {
		for (int i = 0; i < 3; i++)
			mat[i][i] = 1.0f;
	}
}
