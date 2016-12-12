package info;

import java.io.Serializable;

public class PointInfo implements Serializable {
	int pointX;
	int pointY;

	/** PointInfo Construction */
	public PointInfo(int pointX, int pointY) {
		this.pointX = pointX;
		this.pointY = pointY;
	}

	/* getter */
	public int getPointX() {
		return pointX;
	}

	public int getPointY() {
		return pointY;
	}

	/* setter */
	public void setPointX(int pointX) {
		this.pointX = pointX;
	}

	public void setPointY(int pointY) {
		this.pointY = pointY;
	}
}
