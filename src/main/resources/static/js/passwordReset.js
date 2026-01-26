//password変更成功時のメッセージ
function userAuth_checkResetSuccess() {
  const urlParams = new URLSearchParams(window.location.search);

  if (urlParams.has("resetSuccess")) {
    alert("パスワードが正常に変更されました。\n新しいパスワードでログインしてください。");

    const cleanUrl =
      window.location.protocol + "//" + window.location.host + window.location.pathname;
    window.history.replaceState({ path: cleanUrl }, "", cleanUrl);
  }
}

document.addEventListener("DOMContentLoaded", function () {
  userAuth_checkResetSuccess();
});
