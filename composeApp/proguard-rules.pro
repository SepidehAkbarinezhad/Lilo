# Keep warnings/errors for sanitized operational diagnostics; remove debug/info logs.
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# Preserve useful source locations in Crashlytics reports. Library consumer rules
# handle Firebase, Room and serialization; do not keep entire application packages.
-keepattributes SourceFile,LineNumberTable
