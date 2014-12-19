package transforms;
/**
 * trida pro praci s 2D vektory 
 * PGRF 2012
 */


public class Vec2D {
	public double x, y;

	/**
	 * Vytvoreni instance 2D vektoru (0,0)
	 */
	public Vec2D() {
		x = y = 0.0f;
	}

	/**
	 * Vytvoreni instance 2D vektoru (x,y)
	 * 
	 * @param x
	 *            souradnice x
	 * @param y
	 *            souradnice y
	 */
	public Vec2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Vytvoreni instance 2D vektoru (x,y)
	 * 
	 * @param v
	 *            souradnice (x,y)
	 */
	public Vec2D(Vec2D v) {
		x = v.x;
		y = v.y;
	}

	/**
	 * Pricteni vektoru
	 * 
	 * @param v
	 *            vektor (x,y)
	 * @return nova instance Vec2D
	 */
	public Vec2D add(Vec2D v) {
		return new Vec2D(x + v.x, y + v.y);
	}

	/**
	 * Nasobeni skalarem
	 * 
	 * @param d
	 *            skalar
	 * @return nova instance Vec2D
	 */
	public Vec2D mul(double d) {
		return new Vec2D(x * d, y * d);
	}

	/**
	 * Nasobeni vektorem
	 * 
	 * @param v
	 *            vektor (x,y)
	 * @return nova instance Vec2D
	 */
	public Vec2D mul(Vec2D v) {
		return new Vec2D(x * v.x, y * v.y);
	}

	/**
	 * Skalarni soucin vektoru
	 * 
	 * @param v
	 *            vektor (x,y)
	 * @return nova instance Vec2D
	 */
	public double dot(Vec2D v) {
		return x * v.x + y * v.y;
	}

	/**
	 * Normalizace vektoru
	 * 
	 * @return nova instance Vec2D
	 */
	public Vec2D normalized() {
		double l = length();
		if (l == 0.0f)
			return null;
		return new Vec2D(x / l, y / l);
	}

	/**
	 * Velikost vektoru
	 * 
	 * @return velikost
	 */
	public double length() {
		return (double) Math.sqrt((double) (x * x + y * y));
	}

}
