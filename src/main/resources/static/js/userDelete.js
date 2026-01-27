document.addEventListener("DOMContentLoaded", () => {
  // 削除用モーダルの初期設定
  setupModalEvents("confirmModal");

  // 「アカウント削除」ボタンでモーダルを開く
  const openBtn = document.querySelector(".prototype-show-btn-delete");
  if (openBtn) {
    openBtn.addEventListener("click", () => openModal("confirmModal"));
  }

  // 「削除する」実行ボタン
  const executeBtn = document.getElementById("delete-execute-btn");
  if (executeBtn) {
    executeBtn.addEventListener("click", () => {
      const userId = executeBtn.getAttribute("data-user-id");
      executeDelete(userId);
    });
  }

  // 「トップページへ」ボタン
  const indexBtn = document.querySelector(".modal-toppage-btn");
  if (indexBtn) {
    indexBtn.addEventListener("click", () => {
      window.location.href = "/";
    });
  }
});

async function executeDelete(targetId) {
  closeModal("confirmModal");
  try {
    const response = await fetch(`/users/${targetId}/delete`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
    });
    if (response.ok) {
      openModal("successModal");
    } else {
      alert("削除に失敗しました。");
    }
  } catch (error) {
    console.error("Error:", error);
  }
}
