import { MatTable, MatTableDataSource } from "@angular/material/table";
import { Component, ViewChild } from "@angular/core";
import { SelectionModel } from "@angular/cdk/collections";

@Component({
    standalone: true,
    template: ''
})
export abstract class CommonDataSource<E> extends MatTableDataSource<E> {

    @ViewChild(MatTable)
    public table!: MatTable<E>;

    selection = new SelectionModel<E>(true, []);

    abstract getKeyLabel(bean: E): string | number;

    constructor() {
        super();
    }

    getItemReference(bean: E): E | undefined {
        return this.data.find(b => this.equals(b, bean));
    }

    appendItem(bean: E) {
        this.data.push(bean);

        if (this.table) {
            this.table.renderRows();
        }
    }

    prependItem(bean: E) {
        this.data.unshift(bean);

        if (this.table) {
            this.table.renderRows();
        }
    }

    equals(bean1: E, bean2: E): boolean {
        return this.getKeyLabel(bean1) === this.getKeyLabel(bean2);
    }

    removeItem(bean: E) {
        const tmp = this.data.filter(b => !this.equals(b, bean));
        this.data.length = 0

        this.data.push(...tmp)

        if (this.table) {
            this.table.renderRows();
        }
    }

    replaceItemWith(bean: E, _with: E) {
        const index = this.data.findIndex(b => this.equals(b, bean));
        const tmp: Array<E> = [];
        if (index > 0) {
            tmp.push(...this.data.slice(0, index))
        }

        tmp.push(_with);

        if (index < this.data.length - 1) {
            tmp.push(...this.data.slice(index + 1))
        }

        this.data.length = 0;
        this.data.push(...tmp)
        if (this.table) {
            this.table.renderRows();
        }
    }

    setData(data: E[]) {
        this.data = data
        if (this.table) {
            this.table.renderRows();
        }
    }

    /** Whether the number of selected elements matches the total number of rows. */
    isAllSelected() {
        const numSelected = this.selection.selected.length;
        const numRows = this.data != null ? this.data.length : 0;
        return numSelected === numRows;
    }

    /** Selects all rows if they are not all selected; otherwise clear selection. */
    toggleAllRows() {
        if (this.isAllSelected()) {
            this.selection.clear();
            return;
        }
        this.selection.select(...this.data);
    }

    /** The label for the checkbox on the passed row */
    checkboxLabel(row?: E): string {
        if (!row) {
            return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
        }
        return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${this.getKeyLabel(row)}`;
    }
}