package transforms;

public class Mat4RotXYZ extends Mat4Identity {

	/**
	 * Vytvari transformacni matici 4x4 pro rotaci kolem osy X,Y,Z ve 3D
	 * 
	 * @param alpha
	 *            uhel rotace v radianech
	 * @param alpha
	 *            uhel rotace v radianech
	 * @param alpha
	 *            uhel rotace v radianech
	 */
	public Mat4RotXYZ(double alpha, double beta, double gama) {
		Mat4 M=new Mat4RotX(alpha).mul(new Mat4RotY(beta)).mul(new Mat4RotZ(gama));
		for(int i=0;i<4;i++)
			for(int j=0;j<4;j++)
				this.mat[i][j]=M.mat[i][j];
	}
}