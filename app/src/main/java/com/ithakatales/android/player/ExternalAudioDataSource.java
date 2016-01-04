package com.ithakatales.android.player;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import timber.log.Timber;

/**
 * @author farhanali
 *
 * Ref: https://gist.github.com/frostymarvelous/26ac6cba11bf50e591a4
 */
public class ExternalAudioDataSource {

    private final String key;
    private final String iv;
    private final File file;
    private long contentLength;

    public ExternalAudioDataSource(File resource, String key, String iv) {
        //it is your duty to ensure that the file exists
        file = resource;
        this.key = key;
        this.iv = iv;
        contentLength = file.length();
        Timber.i("path: " + file.getPath() + ", exists: " + file.exists() + ", length: " + contentLength);
    }

    /**
     * Discards {@code n} bytes of data from the input stream. This method
     * will block until the full amount has been skipped. Does not close the
     * stream.
     *
     * @param in the input stream to read from
     * @param n  the number of bytes to skip
     * @throws EOFException if this stream reaches the end before skipping all
     *                      the bytes
     * @throws IOException  if an I/O error occurs, or the stream does not
     *                      support skipping
     */
    public void skipFully(InputStream in, long n) throws IOException {
        while (n > 0) {
            long amt = in.skip(n);
            if (amt == 0) {
                // Force a blocking read to avoid infinite loop
                if (in.read() == -1) {
                    throw new EOFException();
                }
                n--;
            } else {
                n -= amt;
            }
        }
    }


    /**
     * Returns a MIME-compatible content type (e.g. "text/html") for the
     * resource. This method must be implemented.
     *
     * @return A MIME content type.
     */
    public String getContentType() {
        return "audio/mpeg";
    }

    /**
     * Returns the length of resource in bytes.
     * <p/>
     * By default this returns -1, which causes no content-type header to be
     * sent to the client. This would make sense for a stream content of
     * unknown or undefined length. If your resource has a defined length
     * you should override this method and return that.
     *
     * @return The length of the resource in bytes.
     */
    public long getContentLength() {
        return contentLength;
    }

    public InputStream getInputStream() {
        try {
            byte[] iv = new byte[16];
            System.arraycopy(this.iv.getBytes(), 0, iv, 0, 16);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            SecretKeySpec sks = new SecretKeySpec(key.getBytes(), "AES");//I use a custom decryption here on a key retrieved from the server

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");///CBC/NoPadding", "BC");//Bouncy Castle is specified. Seems the safest bet on android
            cipher.init(Cipher.DECRYPT_MODE, sks, ivSpec);

            return new CipherInputStream(new FileInputStream(file), cipher);
        } catch (InvalidKeyException e) {
            Timber.e("failed to create input stream", e);
        } catch (NoSuchAlgorithmException e) {
            Timber.e("failed to create input stream", e);
        } catch (NoSuchPaddingException e) {
            Timber.e("failed to create input stream", e);
        } catch (InvalidAlgorithmParameterException e) {
            Timber.e("failed to create input stream", e);
        } catch (Exception e) {
            Timber.e("failed to create input stream", e);
        }

        return null;
    }

}