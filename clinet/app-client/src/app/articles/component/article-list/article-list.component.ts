import {Component} from 'angular2/core';
import {ArticlesStore} from '../../state/articles.store';
import {RouteParams} from 'angular2/router';
import {PaginationComponent} from '../../../common/component/pagination/pagination.component';
import {Router} from 'angular2/router';

@Component({
    selector: 'nv-article-list',
    templateUrl: 'app/articles/component/article-list/article-list.component.html',
    directives: [PaginationComponent]
})
export class ArticleListComponent {
    currentPage:number;

    constructor(private articlesStore:ArticlesStore, params:RouteParams, private router:Router) {
        articlesStore.getArticles(+params.get('offset'), +params.get('limit')).subscribe((e:any)=> {
            console.log(e.meta);
            //let total = e.meta.total;
            this.currentPage = 1;
        });
        this.currentPage = 1;

        console.log('init');

    }

    nextPage() {
        console.log('next');
    }

    changePage(e:any) {

        this.router.navigate(['Articles', {offset: e.offset, limit: e.limit}]);
        console.log(e);
    }
}
