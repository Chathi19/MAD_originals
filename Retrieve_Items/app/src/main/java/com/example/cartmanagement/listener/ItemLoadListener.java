package com.example.cartmanagement.listener;

import com.example.cartmanagement.model.ItemModel;

import java.util.List;

public interface ItemLoadListener {

    void onItemLoadSuccess(List<ItemModel> itemModelList);
    void onItemLoadFailed(String message);
}
