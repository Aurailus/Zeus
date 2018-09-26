package ZeusClient.engine.helpers;

import ZeusClient.game.Main;
import com.keypoint.PngEncoderB;
import de.matthiasmann.twl.utils.PNGDecoder;
import org.joml.Vector4f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class TextureAtlas {
    private ArrayList<BufferedImage> images;
    private int imagePointer = 0;

    private HashMap<String, Vector4f> uvs;

    private int texSize = 0;
    private int widthOffset = 0;
    private int heightOffset = 0;
    private int nextLineOffset = 0;

    private boolean debug;

    public TextureAtlas(int size) {
        this(size, false);
    }

    public TextureAtlas(int size, boolean debug) {
        this.debug = debug;
        texSize = size;

        uvs = new HashMap<>();

        images = new ArrayList<>();
        images.add(new BufferedImage(texSize, texSize, BufferedImage.TYPE_INT_ARGB));
    }

    public void loadTexturesFolder() {
        addTexture("/textures/_missing.png");
        try {
            String[] names = findBlockTextures();
            addTextures(names);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private String[] findBlockTextures() throws URISyntaxException {
        URI uri = Main.class.getResource("/textures/textures").toURI();
        File[] files = new File(uri).listFiles();
        if (files == null) return new String[] {};

        String[] names = new String[files.length];
        for (var i = 0; i < files.length; i++) {
            names[i] = "/textures/textures/" + files[i].getName();
        }

        return names;
    }

    private void addTextureSheet() {
        imagePointer++;
        images.add(new BufferedImage(texSize, texSize, BufferedImage.TYPE_INT_ARGB));
        throw new NullPointerException("Need to update UVs to handle multiple images");
    }

    public void addTextures(String[] textures) {
        for (String t : textures) {
            addTexture(t);
        }
    }

    public void addTexture(String texName) {
        PNGDecoder dec = null;
        try {
            dec = new PNGDecoder(Main.class.getResourceAsStream(texName));
        }
        catch (Exception e) {
            System.err.println("!! Cannot find texture " + texName + " !!");
            e.printStackTrace();
        }

        int start = texName.lastIndexOf("/");
        int end = texName.lastIndexOf(".");

        String name = texName.substring(start+1, end);

        var width = dec.getWidth();
        var height = dec.getHeight();

        int xBase, yBase;


        if (widthOffset + width <= texSize) {
            xBase = widthOffset;
            widthOffset += width;
            yBase = heightOffset;
        }
        else {
            widthOffset = width;
            xBase = 0;
            heightOffset += nextLineOffset;
            yBase = heightOffset;
            nextLineOffset = 0;
        }
        if (heightOffset + height > texSize) {
            xBase = 0;
            yBase = 0;
            heightOffset = 0;
            addTextureSheet();
        }
        if (height > nextLineOffset) nextLineOffset = height;

        uvs.put(name, new Vector4f((float)xBase/texSize, (float)yBase/texSize, (float)(xBase+width)/texSize, (float)(yBase+width)/texSize));

        Graphics gfx = images.get(imagePointer).getGraphics();

        ByteBuffer buf = ByteBuffer.allocateDirect(4 * dec.getWidth() * dec.getHeight());
        try {
            dec.decode(buf, dec.getWidth() * 4, PNGDecoder.Format.RGBA);
        } catch (Exception e) {
            System.err.println("!! Invalid encoding for image " + texName + " !!");
            e.printStackTrace();
        }
        buf.position(0);


        for (var i = 0; i < height; i++) {
            for (var j = 0; j < width; j++) {
                int r = buf.get() & 0xFF;
                int g = buf.get() & 0xFF;
                int b = buf.get() & 0xFF;
                int a = buf.get() & 0xFF;

                gfx.setColor(new Color(r, g, b, a));
                gfx.drawRect(xBase + j, yBase + i, 0, 0);
            }
        }

        gfx.setColor(new Color(255, 255, 0, 128));
        if (debug) {
            gfx.drawLine(xBase, yBase, xBase + width - 1, yBase + height - 1);
            gfx.drawLine(xBase + width - 1, yBase, xBase, yBase + height - 1);
        }
    }

    public void encode() throws IOException {
        for (var i = 0; i < images.size(); i++) {
            PngEncoderB enc = new PngEncoderB(images.get(i), true);

            FileOutputStream fout = new FileOutputStream("atlas_" + i + ".png");
            var bytes = enc.pngEncode();
            fout.write(bytes);
            fout.flush();
            fout.close();
        }
    }

    public Vector4f getTexUV(String tex_name) {
        if (uvs.containsKey(tex_name)) return uvs.get(tex_name);
        else return uvs.get("_missing");
    }
}
