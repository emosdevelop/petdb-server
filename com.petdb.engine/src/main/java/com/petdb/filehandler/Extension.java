package com.petdb.filehandler;

public enum Extension {
    JSON("json"), XML("xml");

    private final String extension;

    Extension(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
