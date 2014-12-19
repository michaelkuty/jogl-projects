package transforms;

public class Mat4Transl extends Mat4Identity {

	/**
	 * Vytvari transformacni matici 4x4 pro translaci ve 3D
	 * 
	 * @param x
	 *            posunuti na ose x
	 * @param y
	 *            posunuti na ose y
	 * @param z
	 *            posunuti na ose z
	 */
	public Mat4Transl(double x, double y, double z) {
		mat[3][0] = x;
		mat[3][1] = y;
		mat[3][2] = z;
	}

}