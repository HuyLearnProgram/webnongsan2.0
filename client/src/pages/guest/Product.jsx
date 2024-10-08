import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useSearchParams } from 'react-router-dom';
import { Breadcrumb, ProductCard, FilterItem } from '../../components';
import { apiGetProducts, apiGetMaxPrice } from '../../apis';
import Masonry from 'react-masonry-css';
import { v4 as uuidv4 } from 'uuid';
import SortItem from '../../components/SortItem';
import { sortProductOption } from '../../utils/constants';
import { ClipLoader } from "react-spinners";

const breakpointColumnsObj = {
  default: 5,
  1100: 4,
  700: 3,
  500: 2
};

// Loading spinner styles
const override = {
  display: "block",
  margin: "0 auto",
};

const Product = () => {
  const [products, setProducts] = useState(null);
  const [activeClick, setActiveClick] = useState(null);
  const [params] = useSearchParams();
  const { category } = useParams();
  const [maxPrice, setMaxPrice] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [isProductLoading, setIsProductLoading] = useState(true);
  const [sortOption, setSortOption] = useState('');
  const [error, setError] = useState(null);

  const fetchMaxPrice = async () => {
    try {
      setIsLoading(true);
      setError(null);
      const res = await apiGetMaxPrice(category);
      if (res.statusCode === 200) {
        setMaxPrice(res.data);
      } else {
        throw new Error('Failed to fetch max price');
      }
    } catch (error) {
      console.error('Error fetching max price:', error);
      setError('Failed to load price range. Please try again later.');
    } finally {
      setIsLoading(false);
    }
  };

  const fetchProducts = async (queries) => {
    try {
      setIsProductLoading(true);
      setError(null);
      const response = await apiGetProducts(queries);
      if (response.data) {
        setProducts(response.data.result);
      } else {
        throw new Error('Failed to fetch products');
      }
    } catch (error) {
      console.error('Error fetching products:', error);
      setError('Failed to load products. Please try again later.');
    } finally {
      setIsProductLoading(false);
    }
  };

  useEffect(() => {
    fetchMaxPrice();
  }, [category]);

  useEffect(() => {
    const sortValue = params.get('sort') || '';
    setSortOption(sortValue);
  }, [params]);
  
  useEffect(() => {
    let queries = {
      page: 1,
      size: 10,
      filter: []
    };

    let ratings = [], priceRange = [];

    if (category) {
      queries.filter.push(`category.name~'${category}'`);
    }

    for (let [key, value] of params.entries()) {
      if (key === 'rating') {
        const ratingValues = value.split('-');
        ratings.push(...ratingValues);
      } else if (key === 'price') {
        const priceValues = value.split('-');
        priceRange.push(...priceValues);
      }
    }

    if (ratings.length > 0) {
      queries.filter.push(`rating >= ${ratings[0]} and rating <= ${ratings[1]}`);
    }

    if (priceRange.length > 0) {
      queries.filter.push(`price >= ${priceRange[0]} and price <= ${priceRange[1]}`);
    }

    if (sortOption) {
      const [sortField, sortDirection] = sortOption.split('-');
      queries.sort = `${sortField},${sortDirection}`;
    }

    if (queries.filter.length > 0) {
      queries.filter = encodeURIComponent(queries.filter.join(' and '));
    } else {
      delete queries.filter;
    }

    fetchProducts(queries);
  }, [params, sortOption, category]);

  const changeActiveFilter = useCallback((name) => {
    if (activeClick === name) setActiveClick(null);
    else setActiveClick(name);
  }, [activeClick]);

  if (error) {
    return (
      <div className="w-full h-[200px] flex items-center justify-center text-red-500">
        {error}
      </div>
    );
  }

  return (
    <div className='w-full'>
      <div className='h-20 flex justify-center items-center bg-gray-100'>
        <div className='w-main'>
          <h3 className='font-semibold uppercase'>{category}</h3>
          <Breadcrumb category={category} />
        </div>
      </div>

      <div className='w-main border p-4 flex justify-between mt-8 m-auto'>
        <div className='w-4/5 flex-auto flex items-center gap-4'>
          <span className='font-semibold text-sm'>Lọc</span>
          {isLoading ? (
            <div className="flex items-center justify-center w-40">
              <ClipLoader
                size={30}
                color={"#123abc"}
                loading={isLoading}
                cssOverride={override}
                aria-label="Loading Spinner"
              />
            </div>
          ) : (
            <FilterItem 
              name='price' 
              activeClick={activeClick} 
              changeActiveFilter={changeActiveFilter} 
              range 
              min={0} 
              max={maxPrice} 
              step={1000} 
            />
          )}
          <FilterItem 
            name='rating' 
            activeClick={activeClick} 
            changeActiveFilter={changeActiveFilter} 
            range 
            min={0} 
            max={5} 
            step={0.5} 
          />
        </div>
        <div className='w-1/5 flex-auto'>
          <SortItem 
            sortOption={sortOption} 
            setSortOption={setSortOption} 
            sortOptions={sortProductOption} 
          />
        </div>
      </div>

      <div className='mt-8 w-main m-auto'>
        {isProductLoading ? (
          <div className="flex items-center justify-center h-[400px]">
            <ClipLoader
              size={50}
              color={"#123abc"}
              loading={isProductLoading}
              cssOverride={override}
              aria-label="Loading Products"
            />
          </div>
        ) : products?.length > 0 ? (
          <Masonry
            breakpointCols={breakpointColumnsObj}
            className="my-masonry-grid flex mx-0"
            columnClassName="my-masonry-grid_column mb-[-20px]"
          >
            {products.map((e) => (
              <ProductCard key={uuidv4()} productData={e} />
            ))}
          </Masonry>
        ) : (
          <div className="flex items-center justify-center h-[200px] text-gray-500">
            No products found
          </div>
        )}
      </div>
      
      <div className='w-full h-[400px]'></div>
    </div>
  );
};

export default Product;