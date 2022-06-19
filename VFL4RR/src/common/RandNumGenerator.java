package common;

import java.util.Random;

import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

public class RandNumGenerator {

	public static MWNumericArray getRandn(int n, int m) {
		Random rnd=new Random();
		double dt1[][] = new double[n][m];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				dt1[j][i] = rnd.nextGaussian();
			}
		}
		return new MWNumericArray(dt1, MWClassID.DOUBLE);

	}
}
