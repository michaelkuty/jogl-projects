package transforms;

public class Mat3RotX extends Mat3Identity {

	/**
	 * Vytvari transformacni matici 3x3 pro rotaci kolem osy X ve 3D
	 * 
	 * @param alpha
	 *            uhel rotace v radianech
	 */
	public Mat3RotX(double alpha) {
		mat[1][1] = (double) Math.cos(alpha);
		mat[2][2] = (double) Math.cos(alpha);
		mat[2][1] = (double) -Math.sin(alpha);
		mat[1][2] = (double) Math.sin(alpha);
	}
}