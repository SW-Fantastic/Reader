function initPage() {
    if (swdc === null || swdc === undefined) {
        alert("无法加载Pager，超链接将会失效")
        return;
    }
    let links = document.querySelectorAll("a");
    if (!links) {
        return;
    }
    for (let item of links) {
        item.onclick = () => {
            try {
                swdc.locate(item.outerHTML);
            } catch (e) {
                alert(e)
            }
        }
    }
}

initPage();