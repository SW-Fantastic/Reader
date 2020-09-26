package org.swdc.reader.config;

import org.swdc.fx.anno.PropResolver;
import org.swdc.fx.properties.PropertiesResolver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ThemeInstaller implements PropResolver<File> {

    @Override
    public void resolve(File data) throws Exception {

        File assets = new File("assets/theme");

        ZipFile zipFile = new ZipFile(data);
        String themeName = data.getName().split("[.]")[0];
        File themeFolder = new File(assets.getAbsolutePath() + File.separator + themeName);
        if (!themeFolder.exists()) {
            themeFolder.mkdirs();
        }
        Enumeration<? extends ZipEntry> entryEnum = zipFile.entries();
        while (entryEnum.hasMoreElements()) {
            ZipEntry entry = entryEnum.nextElement();
            File file = null;
            if (entry.getName().contains(themeName)) {
                file = new File(assets.getAbsolutePath() + File.separator + entry.getName());

            } else {
                file = new File(themeFolder.getAbsolutePath() + File.separator + entry.getName());
            }
            if (entry.isDirectory()) {
                if (!file.exists()) {
                    file.mkdirs();
                }
                continue;
            }
            InputStream inputStream = zipFile.getInputStream(entry);
            FileOutputStream outputStream = new FileOutputStream(file);
            inputStream.transferTo(outputStream);
            inputStream.close();
            outputStream.close();
        }
    }

    @Override
    public String supportName() {
        return "主题包";
    }

    @Override
    public String[] extensions() {
        return new String[]{ "*.zip" };
    }

}
