window.addEventListener('load',()=>{
   const commentForm = document.getElementById('comment-form');
   const commentList = document.querySelector('.prototype-show-comments'); // クラス名に合わせて修正
   const commentInput = document.getElementById('comment-text');

   const errorContainer = document.getElementById('error-container');
   if(!commentForm) return ;

   commentForm.addEventListener('submit', async(e) =>{
      e.preventDefault();

      if(errorContainer) errorContainer.innerHTML='';

      const formData = new FormData(commentForm);
      const action = commentForm.getAttribute('action');
      
      try{
         const response = await fetch(action,{
            method: 'POST',
            body: formData,
         });

         if(response.ok){
            const newCommentHtml = await response.text();

            if(commentList){
               commentList.insertAdjacentHTML('afterbegin',newCommentHtml);
            }else{
               location.reload();
            }
            
            commentInput.value="";
         
         }else{
            const msg = await response.text();

            errorContainer.innerHTML=`
            <div class="new-error">
               <span>${msg}</span>
            </div>`;
         }

      }catch(error){
         console.error('通信に失敗しました',error);
      }
   });
});