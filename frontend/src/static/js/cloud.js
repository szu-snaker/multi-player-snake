import Resource from '@/static/js/Resource.js'
class Cloud{
    constructor(width,height,img,x,y){
        this.width = width || 200;
        this.height = height || 100 ;
        this.img = img || Resource.cloud;
        this.x  = x || 0 ;
        this.y = y || 0 ;
    }
    initCloud(map){
        if(this.dom){
            this.clearDom();
        }
        this.dom = document.createElement('img');
        this.dom.classList.add('cloud');
        this.dom.style.left =  (Math.random()*300 + 50) + "px";
        this.dom.style.top  =  (Math.random()*300 + 50) + "px";
        this.dom.src = this.img;
        this.dom.style.width = `${this.width}px`;
        this.dom.style.height = `${this.height}px`;

        map.appendChild(this.dom);
    }
    clearDom(){
        if(this.dom){
            this.dom.parentNode.removeChild(this.dom);
            this.dom = null;
        }
    }
    movingTo(x,y){
        this.dom.style.left = x + "px";
        this.dom.style.top = y  + "px";
    }
}
export default Cloud;