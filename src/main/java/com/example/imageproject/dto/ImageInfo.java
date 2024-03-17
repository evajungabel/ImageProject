package com.example.imageproject.dto;

import com.example.imageproject.domain.CustomUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ImageInfo {

    private byte[] data;

    private CustomUser customUser;
}
