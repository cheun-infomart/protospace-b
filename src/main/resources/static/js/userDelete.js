function closeWithdrawModal() {
  const modal = document.getElementById("withdrawModal");
  if (modal) {
    modal.style.display = "none";
    const url = new URL(window.location);
    url.searchParams.delete("withdraw");
    window.history.replaceState({}, document.title, url.pathname);
  }
}
