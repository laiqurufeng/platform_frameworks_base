package android.security;

import android.annotation.IntDef;
import android.security.keymaster.KeymasterDefs;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

/**
 * Constraints for {@code AndroidKeyStore} keys.
 *
 * @hide
 */
public abstract class KeyStoreKeyConstraints {
    private KeyStoreKeyConstraints() {}

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag=true, value={Purpose.ENCRYPT, Purpose.DECRYPT, Purpose.SIGN, Purpose.VERIFY})
    public @interface PurposeEnum {}

    /**
     * Purpose of key.
     */
    public static abstract class Purpose {
        private Purpose() {}

        /**
         * Purpose: encryption.
         */
        public static final int ENCRYPT = 1 << 0;

        /**
         * Purpose: decryption.
         */
        public static final int DECRYPT = 1 << 1;

        /**
         * Purpose: signing.
         */
        public static final int SIGN = 1 << 2;

        /**
         * Purpose: signature verification.
         */
        public static final int VERIFY = 1 << 3;

        /**
         * Number of flags defined above. Needs to be kept in sync with the flags above.
         */
        private static final int VALUE_COUNT = 4;

        /**
         * @hide
         */
        public static int toKeymaster(@PurposeEnum int purpose) {
            switch (purpose) {
                case ENCRYPT:
                    return KeymasterDefs.KM_PURPOSE_ENCRYPT;
                case DECRYPT:
                    return KeymasterDefs.KM_PURPOSE_DECRYPT;
                case SIGN:
                    return KeymasterDefs.KM_PURPOSE_SIGN;
                case VERIFY:
                    return KeymasterDefs.KM_PURPOSE_VERIFY;
                default:
                    throw new IllegalArgumentException("Unknown purpose: " + purpose);
            }
        }

        /**
         * @hide
         */
        public static @PurposeEnum int fromKeymaster(int purpose) {
            switch (purpose) {
                case KeymasterDefs.KM_PURPOSE_ENCRYPT:
                    return ENCRYPT;
                case KeymasterDefs.KM_PURPOSE_DECRYPT:
                    return DECRYPT;
                case KeymasterDefs.KM_PURPOSE_SIGN:
                    return SIGN;
                case KeymasterDefs.KM_PURPOSE_VERIFY:
                    return VERIFY;
                default:
                    throw new IllegalArgumentException("Unknown purpose: " + purpose);
            }
        }

        /**
         * @hide
         */
        public static int[] allToKeymaster(int purposes) {
            int[] result = new int[VALUE_COUNT];
            int resultCount = 0;
            int purpose = 1;
            for (int i = 0; i < 32; i++) {
                if ((purposes & 1) != 0) {
                    result[resultCount] = toKeymaster(purpose);
                    resultCount++;
                }
                purposes >>>= 1;
                purpose <<= 1;
                if (purposes == 0) {
                    break;
                }
            }
            return Arrays.copyOf(result, resultCount);
        }

        /**
         * @hide
         */
        public static @PurposeEnum int allFromKeymaster(Collection<Integer> purposes) {
            @PurposeEnum int result = 0;
            for (int keymasterPurpose : purposes) {
                result |= fromKeymaster(keymasterPurpose);
            }
            return result;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({Algorithm.AES, Algorithm.HMAC})
    public @interface AlgorithmEnum {}

    /**
     * Key algorithm.
     */
    public static abstract class Algorithm {
        private Algorithm() {}

        /**
         * Key algorithm: AES.
         */
        public static final int AES = 0;

        /**
         * Key algorithm: HMAC.
         */
        public static final int HMAC = 1;

        /**
         * @hide
         */
        public static int toKeymaster(@AlgorithmEnum int algorithm) {
            switch (algorithm) {
                case AES:
                    return KeymasterDefs.KM_ALGORITHM_AES;
                case HMAC:
                    return KeymasterDefs.KM_ALGORITHM_HMAC;
                default:
                    throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
            }
        }

        /**
         * @hide
         */
        public static @AlgorithmEnum int fromKeymaster(int algorithm) {
            switch (algorithm) {
                case KeymasterDefs.KM_ALGORITHM_AES:
                    return AES;
                case KeymasterDefs.KM_ALGORITHM_HMAC:
                    return HMAC;
                default:
                    throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
            }
        }

        /**
         * @hide
         */
        public static String toString(@AlgorithmEnum int algorithm) {
            switch (algorithm) {
                case AES:
                    return "AES";
                case HMAC:
                    return "HMAC";
                default:
                    throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
            }
        }

        /**
         * @hide
         */
        public static @AlgorithmEnum int fromJCASecretKeyAlgorithm(String algorithm) {
            if (algorithm == null) {
                throw new NullPointerException("algorithm == null");
            } else  if ("AES".equalsIgnoreCase(algorithm)) {
                return AES;
            } else if (algorithm.toLowerCase(Locale.US).startsWith("hmac")) {
                return HMAC;
            } else {
                throw new IllegalArgumentException(
                        "Unsupported secret key algorithm: " + algorithm);
            }
        }

        /**
         * @hide
         */
        public static String toJCASecretKeyAlgorithm(@AlgorithmEnum int algorithm,
                @DigestEnum Integer digest) {
            switch (algorithm) {
                case AES:
                    return "AES";
                case HMAC:
                    if (digest == null) {
                        throw new IllegalArgumentException("HMAC digest not specified");
                    }
                    switch (digest) {
                        case Digest.SHA256:
                            return "HmacSHA256";
                        default:
                            throw new IllegalArgumentException(
                                    "Unsupported HMAC digest: " + digest);
                    }
                default:
                    throw new IllegalArgumentException("Unsupported key algorithm: " + algorithm);
            }
        }

        /**
         * @hide
         */
        public static String toJCAKeyPairAlgorithm(@AlgorithmEnum int algorithm) {
            switch (algorithm) {
                default:
                    throw new IllegalArgumentException("Unsupported key alorithm: " + algorithm);
            }
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({Padding.NONE, Padding.ZERO, Padding.PKCS7})
    public @interface PaddingEnum {}

    /**
     * Padding for signing and encryption.
     */
    public static abstract class Padding {
        private Padding() {}

        /**
         * No padding.
         */
        public static final int NONE = 0;

        /**
         * Pad with zeros.
         */
        public static final int ZERO = 1;

        /**
         * PKCS#7 padding.
         */
        public static final int PKCS7 = 2;

        /**
         * @hide
         */
        public static int toKeymaster(int padding) {
            switch (padding) {
                case NONE:
                    return KeymasterDefs.KM_PAD_NONE;
                case ZERO:
                    return KeymasterDefs.KM_PAD_ZERO;
                case PKCS7:
                    return KeymasterDefs.KM_PAD_PKCS7;
                default:
                    throw new IllegalArgumentException("Unknown padding: " + padding);
            }
        }

        /**
         * @hide
         */
        public static @PaddingEnum int fromKeymaster(int padding) {
            switch (padding) {
                case KeymasterDefs.KM_PAD_NONE:
                    return NONE;
                case KeymasterDefs.KM_PAD_ZERO:
                    return ZERO;
                case KeymasterDefs.KM_PAD_PKCS7:
                    return PKCS7;
                default:
                    throw new IllegalArgumentException("Unknown padding: " + padding);
            }
        }

        /**
         * @hide
         */
        public static String toString(@PaddingEnum int padding) {
            switch (padding) {
                case NONE:
                    return "NONE";
                case ZERO:
                    return "ZERO";
                case PKCS7:
                    return "PKCS#7";
                default:
                    throw new IllegalArgumentException("Unknown padding: " + padding);
            }
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({Digest.NONE, Digest.SHA256})
    public @interface DigestEnum {}

    /**
     * Digests that can be used with a key when signing or generating Message Authentication
     * Codes (MACs).
     */
    public static abstract class Digest {
        private Digest() {}

        /**
         * No digest: sign/authenticate the raw message.
         */
        public static final int NONE = 0;

        /**
         * SHA-256 digest.
         */
        public static final int SHA256 = 1;

        /**
         * @hide
         */
        public static String toString(@DigestEnum int digest) {
            switch (digest) {
                case NONE:
                    return "NONE";
                case SHA256:
                    return "SHA256";
                default:
                    throw new IllegalArgumentException("Unknown digest: " + digest);
            }
        }

        /**
         * @hide
         */
        public static int toKeymaster(@DigestEnum int digest) {
            switch (digest) {
                case NONE:
                    return KeymasterDefs.KM_DIGEST_NONE;
                case SHA256:
                    return KeymasterDefs.KM_DIGEST_SHA_2_256;
                default:
                    throw new IllegalArgumentException("Unknown digest: " + digest);
            }
        }

        /**
         * @hide
         */
        public static @DigestEnum int fromKeymaster(int digest) {
            switch (digest) {
                case KeymasterDefs.KM_DIGEST_NONE:
                    return NONE;
                case KeymasterDefs.KM_DIGEST_SHA_2_256:
                    return SHA256;
                default:
                    throw new IllegalArgumentException("Unknown digest: " + digest);
            }
        }

        /**
         * @hide
         */
        public static @DigestEnum Integer fromJCASecretKeyAlgorithm(String algorithm) {
            String algorithmLower = algorithm.toLowerCase(Locale.US);
            if (algorithmLower.startsWith("hmac")) {
                if ("hmacsha256".equals(algorithmLower)) {
                    return SHA256;
                } else {
                    throw new IllegalArgumentException("Unsupported digest: "
                            + algorithmLower.substring("hmac".length()));
                }
            } else {
                return null;
            }
        }

        /**
         * @hide
         */
        public static String toJCASignatureAlgorithmDigest(@DigestEnum int digest) {
            switch (digest) {
                case NONE:
                    return "NONE";
                case SHA256:
                    return "SHA256";
                default:
                    throw new IllegalArgumentException("Unknown digest: " + digest);
            }
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({BlockMode.ECB})
    public @interface BlockModeEnum {}

    /**
     * Block modes that can be used when encrypting/decrypting using a key.
     */
    public static abstract class BlockMode {
        private BlockMode() {}

        /**
         * Electronic Codebook (ECB) block mode.
         */
        public static final int ECB = 0;

        /**
         * @hide
         */
        public static int toKeymaster(@BlockModeEnum int mode) {
            switch (mode) {
                case ECB:
                    return KeymasterDefs.KM_MODE_ECB;
                default:
                    throw new IllegalArgumentException("Unknown block mode: " + mode);
            }
        }

        /**
         * @hide
         */
        public static @BlockModeEnum int fromKeymaster(int mode) {
            switch (mode) {
                case KeymasterDefs.KM_MODE_ECB:
                    return ECB;
                default:
                    throw new IllegalArgumentException("Unknown block mode: " + mode);
            }
        }

        /**
         * @hide
         */
        public static String toString(@BlockModeEnum int mode) {
            switch (mode) {
                case ECB:
                    return "ECB";
                default:
                    throw new IllegalArgumentException("Unknown block mode: " + mode);
            }
        }
    }
}
