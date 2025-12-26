package com.example.demo.Service;

import com.example.demo.Model.RatingCommentModel;

import java.util.List;

public interface RatingCommentService {

    RatingCommentModel addComment(String guestId, RatingCommentModel req);

    List<RatingCommentModel> getByHouse(String houseId);

    List<RatingCommentModel> getByGuest(String guestId);
}
