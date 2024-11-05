from fastapi import APIRouter, HTTPException
from fastapi.responses import JSONResponse
from services.bert_service import ProductService
from utils.redis_utils import get_cached_data, cache_data
router = APIRouter()
product_service = ProductService()

@router.get('/similar/{product_id}')
def get_similar_products(product_id: int):
    try:
        similar_product_ids = product_service.get_similar_products_by_id(product_id)
        response_data = {"status_code": 200, "data": similar_product_ids}
        return JSONResponse(content=response_data)
    except HTTPException as e:
        raise e

@router.get('/search/{product_name}')
def search(product_name: str):

    product_ids = product_service.search_similar_products(product_name, 20)

    response_data = {
        "status_code": 200,
        "data": product_ids
    }
    
    return JSONResponse(content=response_data)