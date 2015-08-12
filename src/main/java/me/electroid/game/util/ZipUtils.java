package me.electroid.game.util;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtils {

    /**
     * Unzip a file from a url.
     * @param url The url where to download the zip.
     * @param targetDir The target directory for the unzipped file.
     * @return The unzipped file.
     * @throws IOException
     */
    public static File unZip(URL url, File targetDir) throws IOException {
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        InputStream in = new BufferedInputStream(url.openStream(), 1024);
        File zip = File.createTempFile("arc", ".zip", targetDir);
        OutputStream out = new BufferedOutputStream(new FileOutputStream(zip));
        copyInputStream(in, out);
        out.close();
        return unZip(zip, targetDir).get(0);
    }

    private static List<File> unZip(File theFile, File targetDir) throws IOException {
        if (!theFile.exists()) throw new IOException(theFile.getAbsolutePath() + " does not exist");
        if (!buildDirectory(targetDir)) throw new IOException("Could not create directory: " + targetDir);
        List<File> files = new ArrayList<>();
        ZipFile zipFile = new ZipFile(theFile);
        for (Enumeration entries = zipFile.entries(); entries.hasMoreElements();) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            File file = new File(targetDir, File.separator + entry.getName());
            if (!buildDirectory(file.getParentFile())) throw new IOException("Could not create directory: " + file.getParentFile());
            if (!entry.isDirectory()) {
                copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(file)));
            } else {
                if (!buildDirectory(file)) throw new IOException("Could not create directory: " + file);
            }
            files.add(file);
        }
        zipFile.close();
        theFile.delete();
        return files;
    }

    public static void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len = in.read(buffer);
        while (len >= 0) {
            out.write(buffer, 0, len);
            len = in.read(buffer);
        }
        in.close();
        out.close();
    }

    public static boolean buildDirectory(File file) {
        return file.exists() || file.mkdirs();
    }

}