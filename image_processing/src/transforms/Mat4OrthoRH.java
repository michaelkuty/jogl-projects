package transforms;

public class Mat4OrthoRH extends Mat4Identity {

	/**
	 * Vytvari transformacni matici 4x4 pro ortogonalni deformaci zobrazovaciho
	 * objemu
	 * 
	 * @param w
	 *            sirka okna
	 * @param h
	 *            vyska okna
	 * @param zn
	 *            blizke z
	 * @param zf
	 *            vzdalene z
	 */
	public Mat4OrthoRH(double w, double h, double zn, double zf) {
		mat[0][0] = 2.0 / w;
		mat[1][1] = 2.0 / h;
		mat[2][2] = 1.0 / (zn - zf);
		mat[3][2] = zn / (zn - zf);
	}
}
