package pl.springui.components.cache;

public interface CacheKeyGenerator {

	/**
	 * Default cache key for caching the component - can be used with Spring Cache
	 * key generator. Key is based on the request parameters. Session parameters are
	 * not included?
	 * 
	 * @return
	 */
	Object getCacheKey();

}
