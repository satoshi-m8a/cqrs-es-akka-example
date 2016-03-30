import {Component} from 'angular2/core';
import {Input} from 'angular2/core';
import {OnInit} from 'angular2/core';
import {Output} from 'angular2/core';
import {EventEmitter} from 'angular2/core';

@Component({
    selector: 'nv-pagination',
    templateUrl: 'app/common/component/pagination/pagination.component.html'
})
export class PaginationComponent implements OnInit {
    @Input() limit:number;
    @Input() maxItems:number;
    @Output() selectPage = new EventEmitter();
    @Input() maxVisiblePages:number = 9;
    @Input() currentPage:number = 1;

    pageList:Array<number>;
    totalPageNum:number;

    select(page:number) {
        this.selectPage.emit({num: page, offset: page * this.limit, limit: this.limit});
    }

    ngOnInit():any {
        this.totalPageNum = Math.ceil(this.maxItems / this.limit);
        if (this.currentPage >= this.totalPageNum) {
            this.currentPage = this.totalPageNum;
        }

        this.pageList = this.getPageList(this.currentPage, this.totalPageNum);

        console.log('total:' + this.totalPageNum);
        console.log('max:' + this.maxItems);
        console.log('limit:' + this.limit);
    }

    previous() {
        let prev = this.currentPage - 1;
        if (prev < 1) {
            prev = 1;
        }
        this.select(prev);
    }

    next() {
        let next = this.currentPage + 1;
        if (next >= this.totalPageNum) {
            next = this.totalPageNum;
        }
        this.select(next);
    }


    private getPageList(current:number, totalPageNum:number):Array<number> {
        var s = current - Math.floor(this.maxVisiblePages / 2);
        var e = current + Math.floor(this.maxVisiblePages / 2);

        let left = current - Math.floor(this.maxVisiblePages / 2);
        if (left <= 0) {
            e = current + Math.floor(this.maxVisiblePages / 2);
            s = 1;
        }

        let right = current + Math.floor(this.maxVisiblePages / 2);
        if (right >= totalPageNum) {
            e = totalPageNum;
            s = e - this.maxVisiblePages + 1;
        }

        if (s <= 0) {
            s = 1;
        }
        if (e >= totalPageNum) {
            e = totalPageNum;
        }

        let list:number[] = [];
        for (let i = s; i <= e; i++) {
            list.push(i);
        }
        return list;
    }
}
