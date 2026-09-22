(() => {
 const loader=document.getElementById('page-loader');
 window.addEventListener('load',()=>setTimeout(()=>loader?.classList.add('hide'),450));
 const saved=localStorage.getItem('greenday_theme');
 if(saved==='dark') document.documentElement.classList.add('dark');
 document.getElementById('theme-toggle')?.addEventListener('click',()=>{document.documentElement.classList.toggle('dark');localStorage.setItem('greenday_theme',document.documentElement.classList.contains('dark')?'dark':'light')});
 const side=document.querySelector('.navbar');
 if(side) document.body.classList.add('has-sidebar');
 if(localStorage.getItem('greenday_sidebar')==='collapsed') side?.classList.add('collapsed');
 document.getElementById('sidebar-toggle')?.addEventListener('click',()=>{
   side?.classList.toggle('collapsed');
   localStorage.setItem('greenday_sidebar',side?.classList.contains('collapsed')?'collapsed':'expanded');
 });
 const path=location.pathname.replace('/','').split('/')[0]||'dashboard';document.querySelector(`[data-nav="${path}"]`)?.classList.add('active');
 document.querySelectorAll('a[href]').forEach(a=>{const href=a.getAttribute('href');if(href&&href.startsWith('/')&&!href.startsWith('//'))a.addEventListener('click',e=>{if(e.ctrlKey||e.metaKey||href==='/logout')return;document.body.classList.add('page-leaving')})});
})();
