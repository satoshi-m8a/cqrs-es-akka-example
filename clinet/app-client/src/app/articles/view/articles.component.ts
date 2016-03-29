import {Component} from 'angular2/core';
import {ArticleListComponent} from '../component/article-list/article-list.component';
import {ArticlesService} from '../service/articles.service';
import {ArticlesStore} from '../state/articles.store';
@Component({
    templateUrl: 'app/articles/view/articles.component.html',
    directives: [ArticleListComponent],
    providers: [ArticlesService, ArticlesStore]
})
export class ArticlesComponent {

}
