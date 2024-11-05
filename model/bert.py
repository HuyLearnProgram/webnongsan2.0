import os
from dotenv import load_dotenv
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager
from services.bert_service import ProductService
from utils.redis_utils import cache_data
from routes import bert_products_routes as router

load_dotenv()
product_service = ProductService()

# Tính toán embeddings và cache vào Redis
def precompute_embeddings():
    products_df = product_service.get_products_from_db()
    embeddings = product_service.compute_embeddings(products_df)
    cache_data('products', products_df)
    cache_data('product_embeddings', embeddings)

# Thực hiện precompute khi khởi động ứng dụng
@asynccontextmanager
async def lifespan(app: FastAPI):
    precompute_embeddings()
    yield

app = FastAPI(lifespan=lifespan)

SERVER = os.getenv('SERVER')
origins = [
    SERVER
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(router.router)
