/*
 * Copyright (C) 2015 DesolationRom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.app.util.desolation;

import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class IOHelper {

    private static final String TAG = "DesoCore IOHelper";

    private static FilenameFilter mZipFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(".zip");
		}
    };

    private static FileFilter mDirFilter = new FileFilter() {
		public boolean accept(File file) {
			return file.isDirectory();
		}
    };

   public static String[] zipFileFilter(String path){
	File f = new File(path);
	File[] g;
	g = f.listFiles(mDirFilter);
	List<String> ret = new ArrayList<String>();
	for (File a: g){
		File[] h = a.listFiles(mZipFilter);
		for (File o: h){
			if (o.isFile()){
				ret.add(o.getAbsolutePath());
				Log.i(TAG, " Found file: "+o.getAbsolutePath());
			}
		}
	}
	for (File b: g){
		String dir = b.getAbsolutePath();
		Log.i(TAG, " Found Path: "+dir);
		zipFileFilter(dir);
		}
		File[] n = f.listFiles(mZipFilter);
			for (File c: n){
				if (c.isFile()){
					ret.add(c.getAbsolutePath());
					Log.i(TAG, " Found file: "+c.getAbsolutePath());
				}
			}
		String[] ret2 = new String[ret.size()];
		ret2 = ret.toArray(ret2);
		return ret2;
	}

    public static int runStorageCheck(String path) {
		File f = new File(path);
		File[] g = f.listFiles();
		if (g == null){
			return 0;
		} else {
			return 1;
		}
	}
}
