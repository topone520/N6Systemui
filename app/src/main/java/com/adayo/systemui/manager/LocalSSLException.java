package com.adayo.systemui.manager;

import javax.net.ssl.SSLException;

public class LocalSSLException extends SSLException {
        public LocalSSLException(String message) {
            super(message);
        }

        public static final class PdsnException extends LocalSSLException {
            public PdsnException() {
                super("获取本地pdsn失败");
            }
        }

        public static final class VerityException extends LocalSSLException {
            public VerityException() {
                super("本地验证证书验证失败");
            }
        }

        public static final class SelfException extends LocalSSLException {
            private final String extra;

            public SelfException(String extra) {
                super("本地验证证书验证失败");
                this.extra = extra;
            }

            public String getExtra() {
                return extra;
            }
        }
    }