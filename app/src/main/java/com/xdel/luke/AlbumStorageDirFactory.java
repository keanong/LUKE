package com.xdel.luke;

import java.io.File;

/**
 * Created by kean.ong on 09/10/2015.
 */
abstract public class AlbumStorageDirFactory {
    public abstract File getAlbumStorageDir(String albumName);
}
