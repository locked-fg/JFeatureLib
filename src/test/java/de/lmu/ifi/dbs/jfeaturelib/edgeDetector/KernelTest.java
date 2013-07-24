package de.lmu.ifi.dbs.jfeaturelib.edgeDetector;

import de.lmu.ifi.dbs.jfeaturelib.Descriptor;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.util.EnumSet;
import org.junit.Test;
import static org.junit.Assert.*;

public class KernelTest {

    public KernelTest() {
    }

    @Test
    public void testGetKernel() {
        Kernel instance = new Kernel();
        float[] expResult = Kernel.SOBEL;
        float[] result = instance.getKernel();
        assertArrayEquals(expResult, result, 0.0001f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetKernelFail() {
        new Kernel().setKernel(new float[2]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetKernelFail1() {
        Kernel kernel = new Kernel(new float[2]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetKernelFail2() {
        Kernel kernel = new Kernel(new double[2]);
    }

    @Test
    public void testSetKernel() {
        Kernel k = new Kernel();
        k.setKernel(Kernel.PREWITT);
        assertArrayEquals(Kernel.PREWITT, k.kernel, 0.0001f);

        k.setKernel(Kernel.SCHARR);
        assertArrayEquals(Kernel.SCHARR, k.kernel, 0.0001f);

        k.setKernel(Kernel.SOBEL);
        assertArrayEquals(Kernel.SOBEL, k.kernel, 0.0001f);
    }

    @Test
    public void testGetKernelWidth() {
        assertEquals(3, new Kernel(new float[9]).getKernelWidth());
        assertEquals(5, new Kernel(new float[25]).getKernelWidth());
    }

    @Test
    public void testGetTreshold() {
        Kernel k = new Kernel();
        k.setTreshold(0);
        assertEquals(0, k.getTreshold());
        
        k.setTreshold(1);
        assertEquals(1, k.getTreshold());
        
        k.setTreshold(254);
        assertEquals(254, k.getTreshold());
        
        k.setTreshold(255);
        assertEquals(254, k.getTreshold());
    }

}
