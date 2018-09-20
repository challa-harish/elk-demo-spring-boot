app.service("cartService",function(){
  var cart = [];
  return {
    "setCart" : function(obj){
      cart = obj;
    },
    "getCart" : function(){
      if(cart.length > 0)
      {
        return cart = cart.filter(function( element ) {
            return element !== undefined;
        })
      }
      else{
        return [];
      }

    },
    "updateCart":function(i,product) {
      cart[i] = product;
    },
    "removeCart" : function(i) {
      delete cart[i];
    },
    "removeAll" : function() {
      cart = [];
    }
  }
})
app.service("cartFinal",function(){
  var cartTotal = 0;
  return {
    "setCart" : function(total){
      cartTotal = total;
    },
    "getCart" : function(){
        return cartTotal;
    },
  }
});
app.service("search",function(){
  var cartTotal = 0;
  return {
    "setCart" : function(total){
      cartTotal = total;
    },
    "getCart" : function(){
        return cartTotal;
    },
  }
});
app.service("orderService",function(){
  var ordernumber = "";
  return {
    "set" : function(total){
      ordernumber = total;
    },
    "get" : function(){
        return ordernumber;
    },
  }
});
