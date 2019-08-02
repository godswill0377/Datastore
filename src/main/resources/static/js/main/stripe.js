'use strict';

var stripe = Stripe('pk_test_6pRNASCoBOKtIshFeQd4XMUh');

function registerElements( exampleName) {

  var form = document.querySelector('form');

  form.addEventListener('submit', function(e) {

    e.preventDefault();

    // Show a loading screen...

    // Gather additional customer data we may have collected in our form.
    var email = form.querySelector('#' + exampleName + '-email');
    var address1 = form.querySelector('#' + exampleName + '-address');
    var city = form.querySelector('#' + exampleName + '-city');
    var state = form.querySelector('#' + exampleName + '-state');
    var zip = form.querySelector('#' + exampleName + '-zip');

    var cardinfo = {
      email: email ? email.value : undefined,
      address_line1: address1 ? address1.value : undefined,
      address_city: city ? city.value : undefined,
      address_state: state ? state.value : undefined,
      address_zip: zip ? zip.value : undefined,
      number: $("#example2-card-number").val() ? $("#example2-card-number").val() : undefined,
      expiry: $("#example2-card-expiry").val() ? $("#example2-card-expiry").val() : undefined,
      cvc: $("#example2-card-cvc").val() ? $("#example2-card-cvc").val() : undefined
    };

      swal({
              title: "Are you sure to continue?",
              text: "Submit payment.",
              type: "info",
              showCancelButton: true,
              closeOnConfirm: false,
              showLoaderOnConfirm: true,
          },
          function(){
              var opt_type = $('input[name=payment-option]:checked').val();

              if(opt_type == '0') {

                  finalOrder("mycard", cardinfo);
              }
              if(opt_type == '1') {

                  finalOrder("stripe", cardinfo);

              }
          });


  });

}


var elements = stripe.elements({
    fonts: [
      {
        cssSrc: 'https://fonts.googleapis.com/css?family=Source+Code+Pro',
      },
    ],
    // Stripe's examples are localized to specific languages, but if
    // you wish to have Elements automatically detect your user's locale,
    // use `locale: 'auto'` instead.
    locale: window.__exampleLocale
  });

  // Floating labels
  var inputs = document.querySelectorAll('.cell.example.example2 .input');
  Array.prototype.forEach.call(inputs, function(input) {
    input.addEventListener('focus', function() {
      input.classList.add('focused');
    });
    input.addEventListener('blur', function() {
      input.classList.remove('focused');
    });
    input.addEventListener('keyup', function() {
      if (input.value.length === 0) {
        input.classList.add('empty');
      } else {
        input.classList.remove('empty');
      }
    });
  });

  registerElements( 'example2');