<?php
/*
Plugin Name: AJAX Comments
Version: 2.08
Plugin URI: http://ajax-comments.dev.rain.hu/
Description: Post comments quickly without leaving or refreshing the page.
Author: Mike Smullin
Author URI: http://www.mikesmullin.com
*/

if(!function_exists('get_option'))
  require_once('../../../wp-config.php');

define('PLUGIN_AJAXCOMMENTS_FILE', 'ajax-comments.php');
define('PLUGIN_AJAXCOMMENTS_PATH', '/wp-content/plugins/ajax-comments/');

// Echo Dynamic JavaScript (.js)
if(strstr($_SERVER['PHP_SELF'], PLUGIN_AJAXCOMMENTS_PATH.PLUGIN_AJAXCOMMENTS_FILE)
   && isset($_GET['js'])):
header("Content-Type:text/javascript"); ?>
var ajax_comment_loading = false;
function ajax_comments_loading(on) { if(on) {
  ajax_comment_loading = true;
  var f = $('commentform');
  f.submit.disabled = true; // disable submit
  new Insertion.Before(f, '<div id="ajax_comments_loading" style="display:none;">Submitting Comment...</div>'); // create loading

  var l = $('ajax_comments_loading');
  new Effect.Appear(l, { beforeStart: function() { with(l.style) {
    display = 'block';
    margin = '0 auto';
    width = '100px';
    font = 'normal 12px Arial';
    background = 'url(<?=get_settings('siteurl').PLUGIN_AJAXCOMMENTS_PATH?>loading.gif) no-repeat 0 50%';
    padding = '0 0 0 23px';
  }}});
} else {
  new Effect.Fade('ajax_comments_loading', { afterFinish: function() { // hide loading
    Element.remove('ajax_comments_loading'); // dystroy loading
  }});
  $('commentform').submit.disabled = false; // enable submit
  ajax_comment_loading = false;
}}

function rotate_auth_image() {
  var img = $('auth-image'), input = $('code');
  if(img) img.src += '?'+Math.random(); // Change AuthImage
  if(input) input.value = ''; // Reset Code
}

function find_commentlist() {
  var e = $('commentlist');
  if(e == null) {
    var e = document.getElementsByTagName('ol');
    for(var i=0; i<e.length; i++)
      if(e[i].className=='commentlist')
        return e[i];
  } else return e;

  /* commentslist doesn't exist (no posts yet)
  so create it above the commentform and return it */
  var f = $('commentform');
  new Insertion.Before(f, '<ol id="commentlist"></ol>'); // create commentform
  return $('commentlist');
}


function ajax_comments_submit() {
  if(ajax_comment_loading) return false;

  ajax_comments_loading(true);
  var f = $('commentform'), ol = find_commentlist();
  new Ajax.Request('<?=get_settings('siteurl').PLUGIN_AJAXCOMMENTS_PATH.PLUGIN_AJAXCOMMENTS_FILE?>?submit', {
    method: 'post',
    asynchronous: true,
    postBody: Form.serialize(f),
    onLoading: function(request) {
      request['timeout_ID'] = window.setTimeout(function() {
        switch (request.readyState) {
        case 1: case 2: case 3:
          request.abort();
          alert('Comment Error: Timeout\nThe server is taking a long time to respond. Try again in a few minutes.');
          break;
        }
      }, 25000);
    },
    onFailure: function(request) {
      alert((request.status!=406? 'Comment Error '+request.status+' : '+request.statusText+'\n' : '')+request.responseText);
    },
    onComplete: function(request) { ajax_comments_loading(false);
      window.clearTimeout(request['timeout_ID']);
      rotate_auth_image(); // AuthImage
      if(request.status!=200) return;

      f.comment.value=''; // Reset comment

      new Insertion.Bottom(ol, request.responseText);
      var li = ol.lastChild, className = li.className, style = li.style;
      new Effect.BlindDown(li, {
        afterFinish: function() { li.className = className; li.style = style; }
      });
    }
  });
  return false;
}
<?php endif;


// Receive AJAX requests
// and return a new comment LI element
if(strstr($_SERVER['PHP_SELF'], PLUGIN_AJAXCOMMENTS_PATH.PLUGIN_AJAXCOMMENTS_FILE)
   && isset($_GET['submit'])):
  global $comment, $comments, $post, $wpdb, $user_ID, $user_identity, $user_email, $user_url;

  function fail($s) { header('HTTP/1.0 406 Not Acceptable'); die($s); }

  // trim and decode all POST variables
  foreach($_POST as $k => $v)
    $_POST[$k] = trim(urldecode($v));

  // extract & alias POST variables
   extract($_POST, EXTR_PREFIX_ALL, '');

  // get the post comment_status
  $post_status = $wpdb->get_var("SELECT comment_status FROM {$wpdb->posts} WHERE ID = '".$wpdb->escape($_comment_post_ID)."' LIMIT 1;");
  if ( empty($post_status) ) // make sure the post exists
    fail("That post doesn't even exist!");
  if ( $post_status == 'closed' ) // and the post is not closed for comments
    fail("Sorry, comments are closed.");

  // if the user is already logged in
  get_currentuserinfo();
  if ( $user_ID ) {
    $_author = addslashes($user_identity); // get their name
    $_email = addslashes($user_email); // email
    $_url = addslashes($user_url); // and url
  } else if ( get_option('comment_registration') ) // otherwise, if logging in is required
    fail("Sorry, you must login to post a comment.");

  // if a Name and Email Address are required to post comments
  if ( get_settings('require_name_email') && !$user_ID )
    if ( $_author == '' ) // make sure the Name isn't blank
      fail('You forgot to fill-in your Name!');
    elseif ( $_email == '' ) // make sure the Email Address isn't blank
      fail('You forgot to fill-in your Email Address!');
    elseif ( !is_email($_email) ) // make sure the Email Address looks right
      fail('Your Email Address appears invalid. Please try another.');

  if ( $_comment == '' ) // make sure the Comment isn't blank
    fail('You forgot to fill-in your Comment!');

  if ( !checkAICode($_code) && !$user_ID ) // must pass AuthImage Word Verification
    fail('Your Word Verification code did not match the picture. Please try again.');

  // Simple duplicate check
  if($wpdb->get_var("
  SELECT comment_ID FROM {$wpdb->comments}
  WHERE comment_post_ID = '".$wpdb->escape($_comment_post_ID)."'
    AND ( comment_author = '".$wpdb->escape($_author)."'
  ".($_email? " OR comment_author_email = '".$wpdb->escape($_email)."'" : "")."
  ) AND comment_content = '".$wpdb->escape($_comment)."'
  LIMIT 1;"))
    fail("You've said that before. No need to repeat yourself.");

  // Simple flood-protection
  if ( $lasttime = $wpdb->get_var("SELECT comment_date_gmt FROM $wpdb->comments WHERE comment_author_IP = '$comment_author_IP' OR comment_author_email = '".$wpdb->escape($_email)."' ORDER BY comment_date DESC LIMIT 1") ) {
    $time_lastcomment = mysql2date('U', $lasttime);
    $time_newcomment  = mysql2date('U', current_time('mysql', 1));

    if ( ($time_newcomment - $time_lastcomment) < 15 ) {
      do_action('comment_flood_trigger', $time_lastcomment, $time_newcomment);
      fail("Sorry, you can only post a new comment once every 15 seconds. Slow down cowboy.");
    }
  }

  // insert comment into WordPress database
  wp_new_comment(array(
    'comment_post_ID' => $_comment_post_ID,
    'comment_author' => $_author,
    'comment_author_email' => $_email,
    'comment_author_url' => $_url,
    'comment_content' => $_comment,
    'comment_type' => '',
    'user_ID' => $user_ID
  ));

  // if the user is not already logged in and wants to be Remembered
  if ( !$user_ID && isset($_remember) ) { // remember cookie
    setcookie('comment_author_' . COOKIEHASH, $_author, time() + 30000000, COOKIEPATH, COOKIE_DOMAIN);
    setcookie('comment_author_email_' . COOKIEHASH, $_email, time() + 30000000, COOKIEPATH, COOKIE_DOMAIN);
    setcookie('comment_author_url_' . COOKIEHASH, $_url, time() + 30000000, COOKIEPATH, COOKIE_DOMAIN);
  } else { // forget cookie
    setcookie('comment_author_' . COOKIEHASH, '', time() - 30000000, COOKIEPATH, COOKIE_DOMAIN);
    setcookie('comment_author_email_' . COOKIEHASH, '', time() - 30000000, COOKIEPATH, COOKIE_DOMAIN);
    setcookie('comment_author_url_' . COOKIEHASH, '', time() - 30000000, COOKIEPATH, COOKIE_DOMAIN);
  }

  // grab comment as it exists in the WordPress database (after being manipulated by wp_new_comment())
  $comment = $wpdb->get_row("SELECT * FROM {$wpdb->comments} WHERE comment_ID = {$wpdb->insert_id} LIMIT 1;");
  $commentcount = $wpdb->get_var("SELECT COUNT(*) FROM {$wpdb->comments} WHERE comment_post_ID = '".$wpdb->escape($_comment_post_ID)."' LIMIT 1;");
  $post->comment_status = $wpdb->get_var("SELECT comment_status FROM {$wpdb->posts} WHERE ID = '".$wpdb->escape($_comment_post_ID)."' LIMIT 1;");

  // scrape templated comment HTML from /themes directory
  header('Content-type: text/html; charset=utf-8');
  ob_start(); // start buffering output
  $comments = array($comment); // make it look like there is one comment to be displayed
  include(TEMPLATEPATH.'/comments.php'); // now ask comments.php from the themes directory to display it
  $commentout = ob_get_clean(); // grab buffered output
  
  preg_match('#<li (.*?)>(.*)</li>#ims', $commentout, $matches); // Regular Expression cuts out the LI element's HTML

  
  if ($commentcount%2 == 0) $matches[1] = str_replace('class="alt"', 'class=""', $matches[1]); // fixing class="alt" bug :)
  
  // return HTML comment to XML HTTP Request object
  echo '<li '.$matches[1].' style="display:none">'.$a.$matches[2].'</li>';
  
  exit;
endif;


add_action('wp_head','ajax_comments_js'); // Set Hook for outputting JavaScript
function ajax_comments_js() { if(is_single()): ?>
<script type="text/javascript" src="<?=get_settings('siteurl').PLUGIN_AJAXCOMMENTS_PATH?>scriptaculous/prototype.js"></script>
<script type="text/javascript" src="<?=get_settings('siteurl').PLUGIN_AJAXCOMMENTS_PATH?>scriptaculous/scriptaculous.js"></script>
<script type="text/javascript" src="<?=get_settings('siteurl').PLUGIN_AJAXCOMMENTS_PATH.PLUGIN_AJAXCOMMENTS_FILE?>?js"></script>
<? endif; }

add_action('comment_form','ajax_comments_inline_js');
function ajax_comments_inline_js() { ?>
<script type="text/javascript"><!--
$('commentform').onsubmit = ajax_comments_submit;
//--></script>
<? }
?>
