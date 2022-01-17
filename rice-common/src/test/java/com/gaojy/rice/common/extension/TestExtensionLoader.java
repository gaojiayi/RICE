package com.gaojy.rice.common.extension;

import com.gaojy.rice.common.extension.ext.SimpleExt;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author gaojy
 * @ClassName TestExtensionLoader.java
 * @Description TODO
 * @createTime 2022/01/17 21:22:00
 */
@RunWith(JUnit4.class)
public class TestExtensionLoader {

    @Test
    public void testGetExtension() {
        SimpleExt impl = ExtensionLoader.getExtensionLoader(SimpleExt.class).getExtension("SimpleExtImpl1");
        Assert.assertEquals(impl.bang(null, 0), "bang1");
    }

}
