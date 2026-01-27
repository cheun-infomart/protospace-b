document.addEventListener('DOMContentLoaded', () => {
  const fileInput = document.getElementById('image');
  const fileNameDisplay = document.getElementById('file-name-text');
  const previewImage = document.getElementById('image-preview');

  if(fileInput){
    fileInput.addEventListener('change',(e) =>{
    const file=e.target.files[0];

    if(file && file.type.startsWith('image/')){
      fileNameDisplay.textContent = file.name;
    
      const imageUrl = URL.createObjectURL(file);
      previewImage.src = imageUrl;
      previewImage.style.display = 'block';


      previewImage.onload = () =>{
        URL.revokeObjectURL(imageUrl);
      };
    }else{
      fileNameDisplay.textContent ="選択されていません";
      previewImage.style.display = 'none';
      previewImage.src="";
    }
    
    })
  }

  document.getElementById('image').addEventListener('change', function(e) {
  const fileName = e.target.files[0] ? e.target.files[0].name : '未選択';
  document.getElementById('file-name-text').textContent = fileName;
});
});

