// import { createAsyncThunk} from "@reduxjs/toolkit";
// import * as apis from "../../apis";

// //https://techmaster.vn/posts/36779/huong-dan-su-dung-createasyncthunk-trong-redux-toolkit
// export const getProducts = createAsyncThunk("product/products", async () => {
//     const response = await apis.apiGetProducts();
//     console.log(response);

//     if (response.statusCode < 200 || response.statusCode >= 300) {
//          return rejectWithValue(response);
//     }

//     return response.data.result;
// });