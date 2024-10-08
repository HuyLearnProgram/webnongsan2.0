import { configureStore } from "@reduxjs/toolkit";
import appSlice from "./app/appSlice";
//import productSlice from "./product/productSilce";
import storage from "redux-persist/lib/storage"
import {
  persistStore,
  persistReducer,
  FLUSH,
  REHYDRATE,
  PAUSE,
  PERSIST,
  PURGE,
  REGISTER,
} from 'redux-persist'
import userSlice from "./user/userSlice";

const commonConfig = {
  key: 'ogani_shop/user',
  storage
}

const userConfig ={
  ...commonConfig,
  whitelist: ['isLoggedIn', 'token', 'current']
}

export const store = configureStore({
  reducer: {
    app: appSlice,
//    products: productSlice,
    user: persistReducer(userConfig, userSlice)
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER],
        //ignoredPaths: ['app.modalChildren'],
      },
    }),
});


export const persistor = persistStore(store)