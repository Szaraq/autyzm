package pl.osik.autismemotion;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.osik.autismemotion.helpers.CipherHelper;

/**
 * Created by User on 2016-10-16.
 */
public class CipherHelperTest {
    CipherHelper instance;
    final String encryptedAppName = "7bk2uIZpb439X0LdJan94g==";     //TODO FINALLY Zmienić na szyfrowane nowym kluczem i zrobić GIT Ignore (patrz TODO Cipher Helper)

    @BeforeClass
    public void setUpBeforeClass() throws Exception {
        instance = CipherHelper.getInstance();
    }

    @Test
    public void encryptTest() {
        String result = instance.encrypt("AutismEmotion");
        Assert.assertEquals(encryptedAppName, result);
    }

    @Test
    public void decryptTest() {
        String result = instance.decrypt(encryptedAppName);
        Assert.assertEquals("AutismEmotion", result);
    }
}
