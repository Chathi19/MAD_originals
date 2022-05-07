package com.example.cartmanagement.listener;

import com.example.cartmanagement.model.CartModel;
import com.example.cartmanagement.model.ItemModel;

import java.util.List;

public interface ICartLoadListener {

    void onCartLoadSuccess(List<CartModel> cartModelList);
    void onCartLoadFailed(String message);
}
