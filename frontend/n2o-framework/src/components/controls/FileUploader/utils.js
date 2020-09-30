import axios from 'axios';

const Size = {
  0: 'B',
  1: 'KB',
  2: 'MB',
};

export function post(url, file, onProgress, onUpload, onError) {
  axios
    .post(url, file, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      onUploadProgress: onProgress,
    })
    .then(function(response) {
      onUpload(response);
    })
    .catch(function(error) {
      onError(error);
    });
}

export function deleteFile(url, id) {
  axios.delete(`${url}/${id}`);
}

export function convertSize(size, step = 0) {
  if (!size || size === 0) {
    return ' 0B';
  }
  if (size / 1024 > 1) {
    return convertSize(size / 1024, step + 1);
  }
  return Math.round(size) + ' ' + Size[step];
}
