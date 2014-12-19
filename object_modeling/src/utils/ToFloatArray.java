package utils;

import java.util.List;

import transforms.Col;
import transforms.Mat3;
import transforms.Mat4;
import transforms.Point3D;
import transforms.Quat;
import transforms.Vec1D;
import transforms.Vec2D;
import transforms.Vec3D;


public class ToFloatArray {
	
	public static float[] convert(Vec1D vec) {
		float[] result = new float[1];
		result[0] = (float) vec.x;
		return result;
	}

	public static float[] convert(Vec2D vec) {
		float[] result = new float[2];
		result[0] = (float) vec.x;
		result[1] = (float) vec.y;
		return result;
	}

	public static float[] convert(Vec3D vec) {
		float[] result = new float[3];
		result[0] = (float) vec.x;
		result[1] = (float) vec.y;
		result[2] = (float) vec.z;
		return result;
	}

	public static float[] convert(Point3D vec) {
		float[] result = new float[4];
		result[0] = (float) vec.x;
		result[1] = (float) vec.y;
		result[2] = (float) vec.z;
		result[3] = (float) vec.w;
		return result;
	}

	public static float[] convert(Col vec) {
		float[] result = new float[4];
		result[0] = (float) vec.r;
		result[1] = (float) vec.g;
		result[2] = (float) vec.b;
		result[3] = (float) vec.a;
		return result;
	}

	public static float[] convert(Quat vec) {
		float[] result = new float[4];
		result[0] = (float) vec.r;
		result[1] = (float) vec.i;
		result[2] = (float) vec.j;
		result[3] = (float) vec.k;
		return result;
	}

	public static float[] convert(Mat3 mat) {
		float[] result = new float[9];
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				result[i * 3 + j] = ((float) mat.mat[i][j]);
		return result;
	}

	public static float[] convert(Mat4 mat) {
		float[] result = new float[16];
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				result[i * 4 + j] = ((float) mat.mat[i][j]);
		return result;
	}

	public static float[] convert(Object vec) {
		if (vec instanceof Vec1D) return convert((Vec1D) vec);
		if (vec instanceof Vec2D) return convert((Vec2D) vec);
		if (vec instanceof Vec3D) return convert((Vec3D) vec);
		if (vec instanceof Point3D) return convert((Point3D) vec);
		if (vec instanceof Col) return convert((Col) vec);
		if (vec instanceof Quat) return convert((Quat) vec);
		if (vec instanceof Mat3) return convert((Mat3) vec);
		if (vec instanceof Mat4) return convert((Mat4) vec);
		if (vec instanceof float[]) return (float[]) vec;
		return null;
	}

	public static <Element> float[] convert(List<Element> list) {
		if (list.isEmpty())
			return null;
		int size = 0;
		for (Element element : list)
			size += floatSize(element);
		float[] result = new float[size];
		int index = 0;
		for (Element element : list) {
			float[] elementArray = convert(element);
			for (int i = 0; i < elementArray.length; i++)
				result[index++] = elementArray[i];
		}
		return result;
	}
	
	protected static int floatSize(Vec1D vec) { return 1; }
	protected static int floatSize(Vec2D vec) { return 2; }
	protected static int floatSize(Vec3D vec) { return 3; }
	protected static int floatSize(Point3D vec) { return 4; }
	protected static int floatSize(Col vec) { return 4; }
	protected static int floatSize(Quat vec) { return 4; }
	protected static int floatSize(Mat3 vec) { return 9; }
	protected static int floatSize(Mat4 vec) { return 16; }

	protected static int floatSize(Object vec) {
		if (vec instanceof Vec1D) return floatSize((Vec1D )vec);
		if (vec instanceof Vec2D) return floatSize((Vec2D) vec);
		if (vec instanceof Vec3D) return floatSize((Vec3D) vec);
		if (vec instanceof Point3D) return floatSize((Point3D) vec);
		if (vec instanceof Col) return floatSize((Col) vec);
		if (vec instanceof Quat) return floatSize((Quat) vec);
		if (vec instanceof Mat3) return floatSize((Mat3) vec);
		if (vec instanceof Mat4) return floatSize((Mat4) vec);
		if (vec instanceof float[]) return ((float[]) vec).length;
		return 0;
	}
	
}

