import pickle
import redis
from contextlib import contextmanager

@contextmanager
def redis_connection():
    client = redis.StrictRedis(host='localhost', port=6379, db=0)
    try:
        yield client
    finally:
        pass  

def cache_data(key, data):
    with redis_connection() as redis_client:
        redis_client.set(key, pickle.dumps(data))

def get_cached_data(key):
    with redis_connection() as redis_client:
        cached_data = redis_client.get(key)
        if cached_data:
            return pickle.loads(cached_data)
    return None
