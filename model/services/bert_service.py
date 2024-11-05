import os
import numpy as np
import pandas as pd
from dotenv import load_dotenv
from sklearn.metrics.pairwise import cosine_similarity
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from entities.base import Product, Category
from utils.redis_utils import get_cached_data, cache_data
from fastapi import HTTPException
from sentence_transformers import SentenceTransformer


load_dotenv()

DATABASE_URL = os.getenv("DB_URL")

class ProductService:
    def __init__(self):
        self.engine = create_engine(DATABASE_URL)
        self.Session = sessionmaker(bind=self.engine)
        self.model = SentenceTransformer('paraphrase-multilingual-MiniLM-L12-v2')
    
    def get_products_from_db(self):
        session = self.Session()
        try:
            results = (
                session.query(Product, Category)
                .join(Category, Product.category_id == Category.id)
                .all()
            )
            products_data = [{
                'id': product.id,
                'product_name': product.product_name,
                'price': product.price,
                'rating': product.rating,
                'category': category.name,
                'description': product.description
            } for product, category in results]
        finally:
            session.close()
        
        return pd.DataFrame(products_data)
    
    def compute_embeddings(self, products_df):
        products_df['combined_features'] = products_df['product_name'] +" " + products_df['category'] + " " + products_df['description'].fillna('')
        embeddings = self.model.encode(products_df['combined_features'].tolist(), convert_to_tensor=False)
        print(len(embeddings))
        return embeddings

    def find_similar(self, input, products_df, embeddings, n=10):
        input_embeddings = self.model.encode(input, convert_to_tensor=False)
        similar = cosine_similarity([input_embeddings], embeddings)[0]
        top_n_indices = np.argsort(similar)[::-1][:n]
        # Chỉ trả về ID của các sản phẩm tương tự
        similar_product_ids = products_df.iloc[top_n_indices]['id'].tolist()
        return similar_product_ids
    
    def search_similar_products(self, product_name: str, n = 20):
        products_df = get_cached_data('products')
        embeddings = get_cached_data('product_embeddings')

        if products_df is None or embeddings is None:
            products_df = self.get_products_from_db()
            embeddings = self.compute_embeddings(products_df)
            cache_data('products', products_df)
            cache_data('product_embeddings', embeddings)
    
        similar_product_ids = self.find_similar(product_name, products_df, embeddings, n)

        if not similar_product_ids:
            raise HTTPException(status_code=404, detail="Không tìm thấy sản phẩm tương tự")

        return similar_product_ids
    
    def get_similar_products_by_id(self, product_id: int, n=12):
        products_df = get_cached_data('products')
        embeddings = get_cached_data('product_embeddings')

        if products_df is None or embeddings is None:
            products_df = self.get_products_from_db()
            embeddings = self.compute_embeddings(products_df)
            cache_data('products', products_df)
            cache_data('product_embeddings', embeddings)

        product_row = products_df[products_df['id'] == product_id]

        if product_row.empty:
            raise HTTPException(status_code=404, detail="Sản phẩm không tồn tại")

        input_text = product_row.iloc[0]['product_name'] + " " + product_row.iloc[0]['category'] + " " + (product_row.iloc[0]['description'] or '')
        similar_product_ids = self.find_similar(input_text, products_df, embeddings, n + 1)

        # Exclude the product itself from the similar products list
        similar_product_ids = [id_ for id_ in similar_product_ids if id_ != product_id]

        if not similar_product_ids:
            raise HTTPException(status_code=404, detail="Không tìm thấy sản phẩm tương tự")

        return similar_product_ids