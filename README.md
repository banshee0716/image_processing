# Image Processing Application

This Java application offers a range of image processing functions. Below are the key functionalities:

## Features

1. **Convert to Grayscale**
   - Converts a given image to grayscale format.
   - Parameters: `imageName` - The name of the image file (without extension).
   - Exception: Throws `IOException` for file read/write errors.

2. **Convert to Negative**
   - Transforms a grayscale image into its negative.
   - Parameters: `imageName` - The name of the grayscale image file.
   - Exception: Throws `IOException` for file read/write errors.

3. **Gamma Correction**
   - Applies gamma correction to a grayscale image.
   - Parameters: 
     - `imageName` - The name of the grayscale image file.
     - `gamma` - The gamma value for correction.
   - Exception: Throws `IOException` for file read/write errors.

4. **Add Salt and Pepper Noise**
   - Introduces salt and pepper noise to an image.
   - Parameters: `imageName` - The name of the image file.
   - Exception: Throws `IOException` for file read/write errors.

5. **Median Filter**
   - Applies a median filter to an image for noise reduction.
   - Parameters: `imageName` - The name of the noisy image file.
   - Exception: Throws `IOException` for file read/write errors.

6. **Max Filter**
   - Applies a max filter to an image for edge enhancement.
   - Parameters: 
     - `imageName` - The name of the image file.
     - `filterSize` - Size of the filter.
   - Exception: Throws `IOException` for file read/write errors.

7. **Laplacian Filter**
   - Applies a Laplacian filter for edge detection.
   - Parameters: 
     - `imageName` - The name of the image file.
     - `filterSize` - Size of the filter.
   - Exception: Throws `IOException` for file read/write errors.

8. **Otsu Thresholding**
   - Performs Otsu's thresholding for image binarization.
   - Parameters: `imageName` - The name of the image file.
   - Exception: Throws `IOException` for file read/write errors.

## Usage

To use these functions, call them with the appropriate parameters as shown in the `main` method of the application.

## Requirements

- Java Development Kit (JDK)
- Image files to process
