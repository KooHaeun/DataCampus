import random
import numpy as np
import os
import cv2
import glob
from PIL import Image, ImageEnhance, ImageChops
import PIL.ImageOps    

num_augmented_images = 8008

file_path = "C:/Users/heni1/image/handnoright/"
file_names = os.listdir(file_path)
total_origin_image_num = len(file_names)
augment_cnt = 1

for i in range(1, num_augmented_images):
    change_picture_index = random.randrange(1, total_origin_image_num-1)
    print(change_picture_index)
    print(file_names[change_picture_index])
    file_name = file_names[change_picture_index]
    
    origin_image_path = "C:/Users/heni1/image/handnoright/" + file_name
    print(origin_image_path)
    image = Image.open(origin_image_path)
    
    inverted_image = image.transpose(Image.FLIP_LEFT_RIGHT)
    inverted_image.save(file_path + 'inverted_' + str(augment_cnt) + '.jpg')#좌우반전
    rotated_image = image.rotate(random.randrange(-20, 20))
    rotated_image.save(file_path + 'rotated_' + str(augment_cnt) + '.jpg')#기울이기
    enhancer = ImageEnhance.Brightness(image)
    brightness_image = enhancer.enhance(1.8)
    brightness_image.save(file_path + 'bright_' + str(augment_cnt) + '.jpg')#밝기조정
    #기울기
    #cx, cy = 0.1, 0
    #cx, cy = 0, 0.1
    cx, cy = 0, random.uniform(0.0, 0.3)
    shear_image = image.transform(
        image.size,
        method=Image.AFFINE,
        data=[1, cx, 0,
              cy, 1, 0,])
    shear_image.save('_shear.png')

    #확대 축소
    zoom = random.uniform(0.7, 1.3) #0.7 ~ 1.3
    width, height = image.size
    x = width / 2
    y = height / 2
    crop_image = image.crop((x - (width / 2 / zoom), y - (height / 2 / zoom), x + (width / 2 / zoom), y + (height / 2 / zoom)))
    zoom_image = crop_image.resize((width, height), Image.LANCZOS)
    zoom_image.save('_zoom.png')
        
    augment_cnt += 1
