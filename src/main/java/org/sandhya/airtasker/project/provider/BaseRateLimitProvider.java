package org.sandhya.airtasker.project.provider;

public abstract class BaseRateLimitProvider {

    public abstract long isCallPermitted (String key);
}
