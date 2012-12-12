package de.lmu.ifi.dbs.jfeaturelib.shapeFeatures;

import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.util.EnumSet;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Franz
 */
public class SquareModelShapeMatrixTest {

    public SquareModelShapeMatrixTest() {
    }

    @Test
    public void testGetMatrix() {
        ByteProcessor bp = new ByteProcessor(9, 9);
        bp.setBackgroundValue(0);
        bp.setColor(0);
        bp.fill();

        bp.setColor(1);
        bp.drawRect(4, 4, 3, 3);

        SquareModelShapeMatrix m = new SquareModelShapeMatrix(3);
        m.run(bp);
        int[][] result = m.getMatrix();
        assertEquals(3, result.length);
        assertEquals(3, result[0].length);
        assertEquals(3, result[1].length);
        assertEquals(3, result[2].length);

        assertArrayEquals(new int[]{0, 0, 1}, result[0]);
        assertArrayEquals(new int[]{1, 0, 0}, result[1]);
        assertArrayEquals(new int[]{0, 0, 1}, result[2]);
    }

    @Test
    public void testGetMatrix1() {
        ByteProcessor bp = new ByteProcessor(9, 9);
        bp.setBackgroundValue(0);
        bp.setColor(0);
        bp.fill();

        bp.setColor(1);
        bp.drawRect(4, 4, 4, 4);

        SquareModelShapeMatrix m = new SquareModelShapeMatrix(3);
        m.run(bp);
        int[][] result = m.getMatrix();
        assertEquals(3, result.length);
        assertEquals(3, result[0].length);
        assertEquals(3, result[1].length);
        assertEquals(3, result[2].length);

        assertArrayEquals(new int[]{0, 1, 0}, result[0]);
        assertArrayEquals(new int[]{0, 0, 1}, result[1]);
        assertArrayEquals(new int[]{0, 1, 0}, result[2]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetMatrixDimension() {
        SquareModelShapeMatrix instance = new SquareModelShapeMatrix();
        instance.setMatrixDimension(2);
        assertEquals(instance.matrixDimension, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetMatrixDimensionFail1() {
        SquareModelShapeMatrix instance = new SquareModelShapeMatrix();
        instance.setMatrixDimension(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetMatrixDimensionFail2() {
        SquareModelShapeMatrix instance = new SquareModelShapeMatrix();
        instance.setMatrixDimension(-1);
    }
}
