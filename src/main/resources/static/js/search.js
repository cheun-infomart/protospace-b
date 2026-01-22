window.addEventListener('load', ()=>{
  console.log("jsを読み込みました");
  const searchForm = document.getElementById('searchForm');
  const resultArea = document.getElementById('search-results'); 

  //共通の検索処理
  const executeSearch = async(keyword, action) =>{
    if(!searchForm || !resultArea) return;

    const url = `${action}?keyword=${encodeURIComponent(keyword)}`;
    
    try{
      const response = await fetch(url,{
        headers: {
          // Spring側でAjax判定が必要な場合のためのカスタムヘッダー
          'X-Requested-With': 'XMLHttpRequest'
        }
      });
      
      if(response.ok){
        const html = await response.text();
        resultArea.innerHTML = html;
      }

    }catch(error){
      console.error('検索通信に失敗しました', error);
    }

  }
  
  //URLパラメータがあるときの初期実行
  const params = new URLSearchParams(window.location.search);// URLの？以降を取得
  const keywordParam = params.get('keyword');//keywordだけを抜き出す

  if(keywordParam && resultArea){
    executeSearch(keywordParam, '/prototypes/search');
  }

  if(searchForm){
  searchForm.addEventListener('submit', async(e)=>{

    if(resultArea){
    e.preventDefault();
    const formData = new FormData(searchForm);
    const keyword = formData.get('keyword');
    const action = searchForm.getAttribute('action');
    executeSearch(keyword, action);
  }

  });
}
});